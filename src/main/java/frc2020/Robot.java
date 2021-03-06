package frc2020;

import lib.geometry.Pose2d;
import lib.geometry.Rotation2d;
import lib.wpilib.TimedRobot;
import lib.util.*;
//import lib.vision.AimingParameters;
import lib.loops.*;
import lib.subsystems.*;
import lib.SubsystemManager;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc2020.subsystems.Drive;
import frc2020.subsystems.Intake;
import frc2020.subsystems.Inventory;
import frc2020.subsystems.Kicker;
import frc2020.subsystems.LED;
import frc2020.subsystems.Limelight;
import frc2020.subsystems.Pixy;
import frc2020.subsystems.RobotStateEstimator;
import frc2020.subsystems.Turret;
import frc2020.subsystems.Hood;
import frc2020.subsystems.Shooter;
import frc2020.subsystems.Superstructure;
import frc2020.auto.AutoModeExecutor;
import frc2020.auto.modes.AutoModeBase;
import frc2020.hmi.HMI;
import frc2020.statemachines.ClimberStateMachine;
import frc2020.statemachines.SuperstructureStateMachine;
import frc2020.statemachines.SuperstructureStateMachine.SystemState;
import frc2020.states.LEDState;
import frc2020.subsystems.AimingDevice;
import frc2020.subsystems.Climber;

import java.util.Optional;

public class Robot extends TimedRobot {

    private final Looper mEnabledLooper = new Looper();
    private final Looper mDisabledLooper = new Looper();

    private final SubsystemManager mSubsystemManager = SubsystemManager.getInstance();

    //Subsystems

    private final Drive mDrive = Drive.getInstance();
    
    private final RobotStateEstimator mRobotStateEstimator = RobotStateEstimator.getInstance();
    
    private final Intake mIntake = Intake.getInstance();
    private final Turret mTurret = Turret.getInstance();
    private final Hood mHood = Hood.getInstance();
    private final Shooter mShooter = Shooter.getInstance();
    private final Kicker mKicker = Kicker.getInstance();
    private final Climber mClimber = Climber.getInstance();

    private final AimingDevice mAimingDevice = AimingDevice.getInstance();
    private final Inventory mInventory = Inventory.getInstance();
    private final Superstructure mSuperstructure = Superstructure.getInstance();

    private final Limelight mLimelight = Limelight.getInstance();
    private final LED mLED = LED.getInstance();
    private final Pixy mPixy = Pixy.getInstance();

    private final Compressor mCompressor;

    private final HMI mHMI = HMI.getInstance();

    private AutoModeSelector mAutoModeSelector = new AutoModeSelector();
    private AutoModeExecutor mAutoModeExecutor;

    private boolean mCoastDrive = true;
    
    Robot() {
        CrashTracker.logRobotConstruction();
        mCompressor = new Compressor();
    }

    @Override
    public void robotInit() {
        try {
            CrashTracker.logRobotInit();

            mSubsystemManager.setSubsystems(
                mRobotStateEstimator,
                mDrive,
                mIntake,
                mTurret,
                mHood,
                mShooter,
                mKicker,
                mClimber,
                mAimingDevice,
                mInventory,
                mSuperstructure,
                mLimelight,
                mLED,
                mPixy
            );
            
            mSubsystemManager.registerEnabledLoops(mEnabledLooper);
            mSubsystemManager.registerDisabledLoops(mDisabledLooper);

            mAutoModeSelector.updateModeCreator();

            if(!SmartDashboard.containsKey("Disable Shooter")) {
                SmartDashboard.putBoolean("Disable Shooter", false);
            }

            //CameraServer.getInstance().startAutomaticCapture();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void disabledInit() {
        try {
            mEnabledLooper.stop();

            mCompressor.stop();
            mDrive.setBraked(true);
            mTurret.setBraked(false);
            mHood.forceDisable();

            // Reset all auto mode state.
            if (mAutoModeExecutor != null) {
                mAutoModeExecutor.stop();
            }
            mAutoModeSelector.reset();
            mAutoModeSelector.updateModeCreator();
            mAutoModeExecutor = new AutoModeExecutor();

            mDisabledLooper.start();

            SmartDashboard.putBoolean("autoInit", false);
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void autonomousInit() {
        try {
            CrashTracker.logAutoInit();
            enabledInit();
            mAutoModeExecutor.start();
            SmartDashboard.putBoolean("autoInit", true);
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void teleopInit() {
        try {
            if (mAutoModeExecutor != null) {
                mAutoModeExecutor.stop();
            }

            SmartDashboard.putBoolean("autoInit", true);
            enabledInit();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    public void enabledInit() {
        mDisabledLooper.stop();
        
        mCompressor.start();
        mDrive.setBraked(true);
        mTurret.setBraked(true);

        mSuperstructure.setDisabled(false);
        mAimingDevice.setDisabled(false);

        mCoastDrive = false;

        mEnabledLooper.start();
    }

    @Override
    public void testInit() {
        try {
            CrashTracker.logTestInit();
            enabledInit();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void robotPeriodic() {
        try {
            //Output to dashboard
            mSubsystemManager.outputToSmartDashboard();
            mAutoModeSelector.outputToSmartDashboard();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    public static boolean disableShooter = false;
    @Override
    public void disabledPeriodic() {
        try {
            mAutoModeSelector.updateModeCreator();
            
            //Reset Field to Vehicle
            var robotState = RobotState.getInstance();
            robotState.reset(Timer.getFPGATimestamp(), Pose2d.identity());
            mDrive.resetGryo();
            mAimingDevice.stop();

            mSuperstructure.resetStateMachine();
            mClimber.resetStateMachine();

            intakeOn = false;
            aimManual = false;
            enableFlywheel = false;

            disableShooter = SmartDashboard.getBoolean("Disable Shooter", false);

            if(mDrive.getAverageDriveVelocityMagnitude() <= 2) {
                mCoastDrive = true;
            }

            mDrive.setBraked(!mCoastDrive);

            mHood.forceDisable();

            Optional<AutoModeBase> autoMode = mAutoModeSelector.getAutoMode();
            if (autoMode.isPresent() && autoMode.get() != mAutoModeExecutor.getAutoMode()) {
                System.out.println("Set auto mode to: " + autoMode.get().getClass().toString());
                mAutoModeExecutor.setAutoMode(autoMode.get());
            }
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    @Override
    public void autonomousPeriodic() {
        mDrive.setBraked(true);
    }

    @Override
    public void teleopPeriodic() {
        try {
            manualControl();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    //Degrees that all shot altitudes change by when the operator wants to increments or decrements it
    private static double deltaAltitudeOffset = 1;

    private LatchedBoolean lToggleCollect = new LatchedBoolean();
    private LatchedBoolean lBlowOff = new LatchedBoolean();
    private LatchedBoolean lAltitudeInc = new LatchedBoolean();
    private LatchedBoolean lAltitudeDec = new LatchedBoolean();

    private boolean intakeOn = false;
    private boolean aimManual = false;
    private boolean enableFlywheel = false;

    double lastTimestamp = Timer.getFPGATimestamp();
    public void manualControl() {
        double timestamp = Timer.getFPGATimestamp();
        double dt = timestamp - lastTimestamp;
        lastTimestamp = timestamp;

        //Update latched buttons
        boolean toggleCollect = lToggleCollect.update(mHMI.getToggleCollect());
        boolean blowOff = lBlowOff.update(!mHMI.getBlow());
        boolean altitudeInc = lAltitudeInc.update(mHMI.getAltitudeInc());
        boolean altitudeDec = lAltitudeDec.update(mHMI.getAltitudeDec());

        //Shooting location
        ShootingLocation.Location shootingLocation = mHMI.getShootingLocation();
        boolean isShootingLocation = shootingLocation != ShootingLocation.Location.NONE;
        if(isShootingLocation) {
            mSuperstructure.setWantedShootingLocation(shootingLocation);
        } else if(mHMI.getClearShot()) {
            mSuperstructure.setWantedShootingLocation(ShootingLocation.Location.NONE);
        }

        //Drive
        boolean search = mSuperstructure.systemStateIsLimelight() && 
                        (mHood.getAngle() > 45.0 || shootingLocation == ShootingLocation.Location.GREEN);
        mDrive.setBuzzDrive(
            mHMI.getThrottle(), 
            mHMI.getSteer(),
            mHMI.getSpinLeft(),
            mHMI.getSpinRight(),
            mHMI.getAim() && search,
            mHMI.getShoot());

        if(isShootingLocation) {
            enableFlywheel = true;
        } else if(mHMI.getClearShot()) {
            mSuperstructure.setWantIdle();
            enableFlywheel = false;
            aimManual = false;
        }

        // ----- Superstructure Wanted Action ------
        // ----- Intake -----
        // Automatically toggle intake off once there are 3 balls
        if(mSuperstructure.getSystemState() == SuperstructureStateMachine.SystemState.INTAKE_FINISH && 
            intakeOn
        ) {
            mSuperstructure.setWantIdle();
            intakeOn = false;
        }
        if(toggleCollect && !enableFlywheel) {
            if(intakeOn) {
                mSuperstructure.setWantIdle();
                intakeOn = false;
            } else {
                mSuperstructure.setWantIntakeOn();
                intakeOn = true;
            }
        } else {
            // ----- Blow -----
            if(mHMI.getBlow()) {
                mSuperstructure.setWantBlow();
            } else if(blowOff) {
                //This handles what happens when the blow button is released, depending on if intake is toggeled or not.
                if(intakeOn) {
                    //Return to intaking if intake is still toggled on
                    mSuperstructure.setWantIntakeOn();
                } else {
                    //Otherwise just turn off blow
                    mSuperstructure.setWantIdle();
                }
            } else {
                // ----- Aim -----
                if(enableFlywheel && !mHMI.getAim()) {
                    mSuperstructure.setWantEnableFlywheel();
                    intakeOn = false;
                } else if(enableFlywheel && mHMI.getAim()) {
                    if(!mHMI.getShoot()) {
                        if((mHMI.getTurretManual() != 0 || aimManual) && mHMI.getAim()) {
                            mSuperstructure.setWantAimManual();
                            aimManual = true;
                        } else {
                            mSuperstructure.setWantAimLimelight();
                        }
                    } else {
                        // ----- Shoot -----
                        mSuperstructure.setWantShoot();
                    }
                }
            }
        }

        //Altitude offset
        if(altitudeInc) {
            //Increment
            mSuperstructure.changeAltitudeOffset(deltaAltitudeOffset);
        } else if(altitudeDec) {
            //Decrement
            mSuperstructure.changeAltitudeOffset(-deltaAltitudeOffset);
        }

        //Manual brush
        if(mHMI.getBrushForward()) {
            mSuperstructure.setBrushOverride(false);
        } else if(mHMI.getBrushBackward()) {
            mSuperstructure.setBrushOverride(true);
        } else {
            mSuperstructure.stopBrushOverride();
        }

        //Climber
        ClimberStateMachine.WantedAction climberWantedAction = ClimberStateMachine.WantedAction.STOP;
        if(mHMI.getMastUp()) {
            climberWantedAction = ClimberStateMachine.WantedAction.MAST_UP;
        } else if(mHMI.getMastDown()) {
            climberWantedAction = ClimberStateMachine.WantedAction.MAST_DOWN;
        } else if(mHMI.getWinchIn()) {
            climberWantedAction = ClimberStateMachine.WantedAction.WINCH_IN;
        } else if(mHMI.getWinchOut()) {
            climberWantedAction = ClimberStateMachine.WantedAction.WINCH_OUT;
        }
        mClimber.setWantedAction(climberWantedAction);

        // LEDs
        
        // TODO clean up
        /*
        if(mClimber.getPartyMode()) {
            mLED.setState(Color.WHITE, 0, true);
        } else {
            if(mSuperstructure.systemStateIsIntaking()) {
                mLED.setState(Color.WHITE, mInventory.getBallCount(), false);
            } else {
                if(mSuperstructure.getSystemState() == SystemState.ENABLE_FLYWHEEL) {
                    mLED.setState(Color.BLUE, 5, false);
                } else {
                    if(mSuperstructure.getSystemState() == SystemState.AIM_LIGHTLIGHT &&
                        mSuperstructure.getAtRPM()
                    ) {
                        if(Math.abs(mDrive.getAutoSteerError()) <= 0.00827266) {
                            mLED.setState(Color.GREEN, 5, false);
                        } else {
                            mLED.setState(Color.BLUE, 5, false);
                        }
                    }
                }
            }
        }
        */
    }

    @Override
    public void testPeriodic() {
        mSuperstructure.setDisabled(true);
        mAimingDevice.setDisabled(true);
        
        //mShooter.setTargetRPM(9000);
        mHood.setPercent(1.0);
        //mKicker.setDemand(12);
    }
}
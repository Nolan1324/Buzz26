/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc2020;

public final class Constants {

    // ------- CHANGE THIS VALUE DEPENDING ON WHICH ROBOT THIS IS -------
    public static final WhichRobot kWhichRobot = WhichRobot.PAT;

    // region ------ Device constants ------
    /** Encoder counts per revolution of the Falcon motor integrated encoder */
    public static final int kFalconCPR = 2048;
    /** rps/V */
    public static final double kFalconKV = 6380d / 12 / 60; //Free speed RPM / nominal voltage / 60 seconds
    /** V/A */
    public static final double kFalconKA = 12d / 257; //Nominal voltage / stall current
    /** Encoder counts per revolution of the CANCoder when used as an encoder */
    public static final int kCANCoderCPR = 4096;
    // endregion -----------------------------

    // region ------ Drive constants and path following ------
    public static final double kDriveWheelTrackWidthInches = 26.6;
    public static final double kDriveWheelDiameterInches = 6;
    public static final double kTrackScrubFactor = 1.0; //Priorly 1.0469745223

    public static final double kMinLookAhead = 12.0; // inches
    public static final double kMinLookAheadSpeed = 12.0; // inches per second
    public static final double kMaxLookAhead = 48.0; // inches
    public static final double kMaxLookAheadSpeed = 150.0; // inches per second

    public static final double kInertiaSteeringGain = 0.0012; // angular velocity command is multiplied by this gain *
                                                     // our speed
                                                     // in inches per sec
    public static final double kPathFollowingMaxAccel = 100.0;  // inches per second ^ 2
    public static final double kPathFollowingMaxVel = 150.0; // inches per second

    public static final double kPathFollowingProfileKp = 0.327 / 12.0;  // % throttle per inch of error
    public static final double kPathFollowingProfileKi = 0.0;
    public static final double kPathFollowingProfileKv = 0.01 / 12.0;  // % throttle per inch/s of error
    public static final double kPathFollowingProfileKffv = 0.0631 / 12.0;  // % throttle per inch/s
    public static final double kPathFollowingProfileKffa = 0.0072 / 12;  // % throttle per inch/s^2
    public static final double kPathFollowingProfileKs = 0.714 / 12.0;  // % throttle

    public static final double kPathFollowingGoalPosTolerance = 3.0; //3.0
    public static final double kPathFollowingGoalVelTolerance = 12.0;
    public static final double kPathStopSteeringDistance = 12.0;
    // endregion ---------------------------------------------

    // TeleOp drive
    /** Set this to true to drive with one joystick */
    public static final boolean kDriveOneJoystick = false;
    public static final double kDriveGearReduction = 11.3666;
    public static final double kQuickTurnEnableThrottle = 0.15;
    public static final double kQuickTurnGain = 0.9;
    public static final double kSpeedTurnGain = 0.03937;
    public static final double kSpeedTurnGainIntaking = 0.03937;
    public static final double kDriveCurrentLimit = 60;
    /** If yaw rate is calculated correctly (deg/s) in {@link frc2020.subsystems.Drive#readPeriodicInputs()}, this should stay as 1 */
    public static final double kYawFactor = 1;
    /** V/deg/s */
    public static final double kYawControlGain = 0.01;
    /** V */
    public static final double kYawMaxCorrection = 4;
    public static final double kYawLead = 0.3;
    public static final double kYawLag = 0.5;
    public static final double kAutoSteerKp = 8;
    public static final double kAutoSteerKi = 0.8;
    public static final double kAutoSteerKd = 0.35;
    public static final double kAutoSteerKiZone = 3;
    public static final double kAutoSteerMaxOutput = 8;
    
    // Intake
    public static final double kIntakeStallCurrent = 90;
    /** Seconds intake motor(s) have to be at stall current to be considered stalled */
    public static final double kIntakeStallTime = 0.15;

    // Turret
    public static final int kTurretTeethA = 12;
    public static final int kTurretTeethB = 13;
    public static final int kTurretTeethRing = 120;
    public static final double kTurretSensorScale = ((double) (kTurretTeethA * kTurretTeethB) / kTurretTeethRing) * 360d;
    public static final RobotConstant<Double> kTurretSensorOffset = new RobotConstant<>(0.1924, 0.0);
    public static final double kTurretAngleMin = -38;
    public static final double kTurretAngleMax = 180;
    public static final double kTurretPeakOutput = 0.35;

    // Hood
    public static final double kHoodEncoderReduction = 2;
    public static final RobotConstant<Double> kHoodSensorOffset = new RobotConstant<>(0.72, 0.0);
    public static final double kHoodAngleOffset = 25;
    public static final double kHoodAngleMin = 27;
    public static final double kHoodAngleMax = 70;
    public static final double kHoodKp = 0.5;
    public static final double kHoodKi = 0.03;
    public static final double kHoodKd = 0;
    public static final double kHoodKiZone = 1;

    // Shooter
    public static final double kShooterGearReduction = 1 / 1.5;
    public static final double kShooterDiameter = 4.0;
    public static final double kShooterRampRate = 0;
    public static final int kShooterSlotIdx = 0;
    public static final double kShooterKp = 0.4;
    public static final double kShooterKi = 0;
    public static final double kShooterKd = 0;
    public static final double kShooterKf = 0.0509952;
    
    // region ------ Device IDs ------
    // Talons
    public static final int kLeftDriveLeaderId = 2;
    public static final int kLeftDriveFollowerId = 3;
    public static final int kRightDriveLeaderId = 4;
    public static final int kRightDriveFollowerId = 5;

    public static final int kIntake1Id = 7;
    public static final int kIntake2Id = 8;
    public static final int kInfeederId = 9;
    public static final int kBrushId = 10;

    public static final int kAzimuthId = 11;

    public static final int kShooterAId = 12;
    public static final int kShooterBId = 13;
    
    public static final int kKickerId = 14;

    public static final int kWinchId = 15;
    public static final int kMastId = 16;

    // CANCoders
    public static final int kTurretEncoderAId = 31;
    public static final int kTurretEncoderBId = 32;
    public static final int kHoodEncoderId = 33;

    // PWM
    public static final int kLEDId = 0;
    public static final int kHoodAId = 1;
    public static final int kHoodBId = 2;

    // DIO
    public static final int kBallSensorIds[] = {0,1,2,3,4};
    public static final int kMastSwitch = 7;
    public static final int kPixyDigitalInputId = 5;
    public static final int kPixyAnalogInputId = 0;
    
    // Solins
    public static final int kIntakeForwardId = 0;
    public static final int kIntakeReverseId = 1;
    // endregion ------------------------ -

    public static enum WhichRobot {
        PAT, VANNA
    } 

    public static class RobotConstant<T> {
        T mPatConstant;
        T mVannaConstant;

        public RobotConstant(T patConstant, T vannaConstant) {
            mPatConstant = patConstant;
            mVannaConstant = vannaConstant;
        }

        public T get() {
            return kWhichRobot == WhichRobot.VANNA ? mVannaConstant : mPatConstant;
        }
    } 
}
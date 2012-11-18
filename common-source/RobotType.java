package battlecode.common;

/**
 * RobotType represents the type of Robot objects that can exist in the game.
 */
public enum RobotType {
	ARCHON(false, 10000, 1.0, 0, 0, true, 0, false, 8, 0.0, false, 360.0, 75.0, 1, 6, 6, 0.0, 0, 0.0, 0, 40),
	WOUT(true, 5000, 5.0, 2.5, 2, false, 0, true, 8, 0.15, false, 360.0, 30.0, 1, 3, 5, 9.0, 5, 360.0, 0, 20),
	CHAINER(true, 250, 5.0, 4, 9, false, 0, true, 8, 0.3, false, 135.0, 50.0, 1, 5, 3, 18.0, 5, 90.0, 0, 40),
	SOLDIER(true, 250, 5.0, 7, 2, false, 0, true, 8, 0.2, false, 180.0, 40.0, 1, 4, 3, 12.0, 5, 180.0, 0, 40),
	TURRET(true, 250, 5.0, .6, 25, false, 0, true, 8, 0.25, false, 90.0, 40.0, 1, 5, 4, 18.0, 1, 45.0, 4, 40),
	COMM(false, 5000, 5.0, 0, 0, false, 3000, false, 14, 0.00, true, 360.0, 500.0, 1, 0, 11, 0.0, 0, 0.0, 0, 40),
	TELEPORTER(false, 3000, 5.0, 0, 0, false, 3000, false, 8, 0.00, true, 360.0, 300.0, 1, 0, 3, 0.0, 0, 0.0, 0, 40),
	AURA(false, 3000, 5.0, 0, 0, false, 3000, false, 8, 0.00, true, 360.0, 300.0, 1, 0, 4, 0.0, 0, 0.0, 0, 40);


	private	final	boolean	_canAttackGround;
	private	final	double	_maxFlux;
	private	final	double	_startEnergon;
	private	final	double	_attackPower;
	private	final	int	_attackRadiusMaxSquared;
	private	final	double	_sensorCosHalfTheta;
	private	final	int	_moveDelayDiagonal;
	private	final	boolean	_isAirborne;
	private	final	int	_spawnFluxCost;
	private	final	boolean	_canAttackAir;
	private	final	int	_broadcastRadius;
	private	final	double	_energonUpkeep;
	private	final	boolean	_isBuilding;
	private	final	double	_sensorAngle;
	private	final	double	_maxEnergon;
	private	final	int	_spawnDelay;
	private	final	int	_moveDelayOrthogonal;
	private	final	int	_sensorRadius;
	private	final	double	_spawnCost;
	private	final	int	_attackDelay;
	private	final	double	_attackAngle;
	private	final	double	_attackCosHalfTheta;
	private	final	int	_attackRadiusMinSquared;
	private	final	int	_wakeDelay;


	RobotType(boolean canAttackGround, double maxFlux, double startEnergon, double attackPower, int attackRadiusMaxSquared, boolean isAirborne, int spawnFluxCost, boolean canAttackAir, int broadcastRadius, double energonUpkeep, boolean isBuilding, double sensorAngle, double maxEnergon, int spawnDelay, int moveDelayOrthogonal, int sensorRadius, double spawnCost, int attackDelay, double attackAngle, int attackRadiusMinSquared, int wakeDelay) {
		_canAttackGround	=	canAttackGround;
		_maxFlux	=	maxFlux;
		_startEnergon	=	startEnergon;
		_attackPower	=	attackPower;
		_attackRadiusMaxSquared	=	attackRadiusMaxSquared;
		_sensorCosHalfTheta	=	(Math.cos((sensorAngle/2.0)*Math.PI/180.0));
		_moveDelayDiagonal	=	((int)Math.round((double)moveDelayOrthogonal*Math.sqrt(2.0)));
		_isAirborne	=	isAirborne;
		_spawnFluxCost	=	spawnFluxCost;
		_canAttackAir	=	canAttackAir;
		_broadcastRadius	=	broadcastRadius;
		_energonUpkeep	=	energonUpkeep;
		_isBuilding	=	isBuilding;
		_sensorAngle	=	sensorAngle;
		_maxEnergon	=	maxEnergon;
		_spawnDelay	=	spawnDelay;
		_moveDelayOrthogonal	=	moveDelayOrthogonal;
		_sensorRadius	=	sensorRadius;
		_spawnCost	=	spawnCost;
		_attackDelay	=	attackDelay;
		_attackAngle	=	attackAngle;
		_attackCosHalfTheta	=	(Math.cos((attackAngle/2.0)*Math.PI/180.0));
		_attackRadiusMinSquared	=	attackRadiusMinSquared;
		_wakeDelay	=	wakeDelay;
	}

	/**
	 * returns the angle of this robot's attack range.
	 * @return the angle of this robot's attack range.
	 */
	public double attackAngle() { return _attackAngle; }

	/**
	 * returns the cosine of half the attack angle.
	 * @return the cosine of half the attack angle.
	 */
	public double attackCosHalfTheta() { return _attackCosHalfTheta; }

	/**
	 * returns the amount of rounds this robot will take to attack.
	 * @return the amount of rounds this robot will take to attack.
	 */
	public int attackDelay() { return _attackDelay; }

	/**
	 * returns the amount of energon this robot will drain from an enemy during an attack.
	 * @return the amount of energon this robot will drain from an enemy during an attack.
	 */
	public double attackPower() { return _attackPower; }

	/**
	 * returns the square of the maximum distance from this robot's location to a MapLocation in its attack range.
	 * @return the square of the maximum distance from this robot's location to a MapLocation in its attack range.
	 */
	public int attackRadiusMaxSquared() { return _attackRadiusMaxSquared; }

	/**
	 * returns the square of the minimum distance from this robot's location to a MapLocation in its attack range.
	 * @return the square of the minimum distance from this robot's location to a MapLocation in its attack range.
	 */
	public int attackRadiusMinSquared() { return _attackRadiusMinSquared; }

	/**
	 * returns the maximum distance from this robot another robot can be to receive broadcasted messages.
	 * @return the maximum distance from this robot another robot can be to receive broadcasted messages.
	 */
	public int broadcastRadius() { return _broadcastRadius; }

	/**
	 * returns whether this robot will inflict damage on an airborne robot at a MapLocation that this robot attacks.
	 * @return whether this robot will inflict damage on an airborne robot at a MapLocation that this robot attacks.
	 */
	public boolean canAttackAir() { return _canAttackAir; }

	/**
	 * returns whether this robot will inflict damage on a ground robot at a MapLocation that this robot attacks.
	 * @return whether this robot will inflict damage on a ground robot at a MapLocation that this robot attacks.
	 */
	public boolean canAttackGround() { return _canAttackGround; }

	/**
	 * returns the amount of energon this robot type loses automatically each turn.
	 * @return the amount of energon this robot type loses automatically each turn.
	 */
	public double energonUpkeep() { return _energonUpkeep; }

	/**
	 * returns whether this robot is airborne.
	 * @return whether this robot is airborne.
	 */
	public boolean isAirborne() { return _isAirborne; }

	public boolean isBuilding() { return _isBuilding; }

	/**
	 * returns the maximum energon level of this robot (not including upgrades).
	 * @return the maximum energon level of this robot (not including upgrades).
	 */
	public double maxEnergon() { return _maxEnergon; }

	public double maxFlux() { return _maxFlux; }

	/**
	 * returns the number of rounds this robot will take to move in a diagonal Direction.
	 * @return the number of rounds this robot will take to move in a diagonal Direction.
	 */
	public int moveDelayDiagonal() { return _moveDelayDiagonal; }

	/**
	 * returns the number of rounds this robot will take to move in an orthogonal (non-diagonal) Direction.
	 * @return the number of rounds this robot will take to move in an orthogonal (non-diagonal) Direction.
	 */
	public int moveDelayOrthogonal() { return _moveDelayOrthogonal; }

	/**
	 * returns the angle of this robot's sensor range.
	 * @return the angle of this robot's sensor range.
	 */
	public double sensorAngle() { return _sensorAngle; }

	/**
	 * returns the cosine of half the sensor angle.
	 * @return the cosine of half the sensor angle.
	 */
	public double sensorCosHalfTheta() { return _sensorCosHalfTheta; }

	/**
	 * returns the maximum distance from this robot's location to a MapLocation in its sensor range.
	 * @return the maximum distance from this robot's location to a MapLocation in its sensor range.
	 */
	public int sensorRadius() { return _sensorRadius; }

	/**
	 * returns the amount of energon needed by another robot to spawn (or evolve into) this robot type.
	 * @return the amount of energon needed by another robot to spawn (or evolve into) this robot type.
	 */
	public double spawnCost() { return _spawnCost; }

	/**
	 * returns the number of rounds for which a robot is active after spawning this type.
	 * @return the number of rounds for which a robot is active after spawning this type.
	 */
	public int spawnDelay() { return _spawnDelay; }

	public int spawnFluxCost() { return _spawnFluxCost; }

	/**
	 * returns the initial energon level of this robot.
	 * @return the initial energon level of this robot.
	 */
	public double startEnergon() { return _startEnergon; }

	/**
	 * returns the number of rounds for which this robot is active after being spawned.
	 * @return the number of rounds for which this robot is active after being spawned.
	 */
	public int wakeDelay() { return _wakeDelay; }

	public int sensorRadiusSquared() { return _sensorRadius*_sensorRadius; }

}

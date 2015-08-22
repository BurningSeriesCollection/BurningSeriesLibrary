<?php

// Table structure:
// [url] |[sessionID] | [dateline] | [data]

class DBCacher extends BurningSeries
{
	/** @var string $host */
	private static $host = 'localhost';
	/** @var string $username */
	private static $username = 'root';
	/** @var string $password */
	private static $password = '';
	/** @var string $databaseName */
	private static $databaseName = 'bs';
	/** @var string $tableName */
	private static $tableName = 'bs_cache';
	/** @var string $urlColumn */
	private static $urlColumn = 'url';
	/** @var string $sessionColumn */
	private static $sessionColumn = 'sessionID';
	/** @var string $datelineColumn */
	private static $datelineColumn = 'dateline';
	/** @var string $dataColumn */
	private static $dataColumn = 'data';
	/** @var PDO $connection */
	private static $connection = null;
	/** @var int $cutoff */
	private static $cutoff = 3600; // One hour

	/**
	 * Set the database credentials used for login
	 *
	 * @param string $username
	 * @param string $password
	 * @param string $databaseName
	 * @param string $tableName
	 * @param string $host
	 */
	public static function setDatabaseCredentials($username, $password, $databaseName = 'bs', $tableName = 'bs_cache', $host = 'localhost')
	{
		static::$username = $username;
		static::$password = $password;
		static::$databaseName = $databaseName;
		static::$tableName = $tableName;
		static::$host = $host;
	}

	/**
	 * @param string $urlColumn
	 */
	public static function setUrlColumn($urlColumn)
	{
		static::$urlColumn = $urlColumn;
	}

	/**
	 * @return string
	 */
	public static function getUrlColumn()
	{
		return static::$urlColumn;
	}

	/**
	 * @param string $sessionColumn
	 */
	public static function setSessionColumn($sessionColumn)
	{
		static::$sessionColumn = $sessionColumn;
	}

	/**
	 * @return string
	 */
	public static function getSessionColumn()
	{
		return static::$sessionColumn;
	}

	/**
	 * @param string $datelineColumn
	 */
	public static function setDatelineColumn($datelineColumn)
	{
		static::$datelineColumn = $datelineColumn;
	}

	/**
	 * @return string
	 */
	public static function getDatelineColumn()
	{
		return static::$datelineColumn;
	}

	/**
	 * @param string $dataColumn
	 */
	public static function setDataColumn($dataColumn)
	{
		static::$dataColumn = $dataColumn;
	}

	/**
	 * @return string
	 */
	public static function getDataColumn()
	{
		return static::$dataColumn;
	}

	/**
	 * Sets the time (in seconds) the cache is valid. Afterwards the API is called again
	 *
	 * @param int $cutoff The cutoff in seconds
	 */
	public static function setCutoff($cutoff)
	{
		static::$cutoff = $cutoff;
	}

	/**
	 * Returns the current cutoff in seconds
	 *
	 * @return int
	 */
	public static function getCutoff()
	{
		return static::$cutoff;
	}

	/**
	 * {@inheritdoc}
	 */
	protected function putCache($url, $data)
	{
		$data = json_encode($data);

		$connection = static::getDatabase();

		$sessionID = $this->getSessionId();
		$time = time();
		$query = $connection->prepare(
			"INSERT INTO " . static::$tableName . " (
				" . static::$urlColumn . ",
				" . static::$sessionColumn . ",
				" . static::$datelineColumn . ",
				" . static::$dataColumn . "
			) VALUES (:url, :session, :dateline, :data)"
		);
		$query->bindParam(':url', $url);
		$query->bindParam(':session', $sessionID);
		$query->bindParam(':dateline', $time, PDO::PARAM_INT);
		$query->bindParam(':data', $data);
		$query->execute();
	}

	/**
	 * {@inheritdoc}
	 */
	protected function hasCache($url)
	{
		$connection = static::getDatabase();

		// First delete old records
		$cutoff = time() - static::$cutoff;
		$query = $connection->prepare(
			"DELETE FROM " . static::$tableName . " WHERE " . static::$datelineColumn . "< :cutoff"
		);
		$query->bindParam(':cutoff', $cutoff, PDO::PARAM_INT);
		$query->execute();

		// Now get "our" record
		$sessionID = $this->getSessionId();
		$sql = "SELECT " . static::$dataColumn . " FROM " . static::$tableName . " WHERE " . static::$urlColumn . " = :url AND " . static::$sessionColumn;
		if($sessionID !== null)
		{
			$sql .= " = :session";
		}
		else
		{
			$sql .= " IS NULL ";
		}
		$query = $connection->prepare($sql);
		$query->bindParam(':url', $url);

		if($sessionID !== null)
		{
			$query->bindParam(':session', $sessionID);
		}

		if(!$query->execute())
		{
			return false;
		}

		$data = $query->fetchColumn();

		if($data === false)
		{
			return false;
		}

		$data = @json_decode($data, true);

		if($data === false)
		{
			return false;
		}

		return true;
	}

	/**
	 * {@inheritdoc}
	 */
	protected function getCache($url)
	{
		$connection = static::getDatabase();

		// Now get "our" record
		$sessionID = $this->getSessionId();
		$sql = "SELECT " . static::$dataColumn . " FROM " . static::$tableName . " WHERE " . static::$urlColumn . " = :url AND " . static::$sessionColumn;
		if($sessionID !== null)
		{
			$sql .= " = :session";
		}
		else
		{
			$sql .= " IS NULL ";
		}
		$query = $connection->prepare($sql);
		$query->bindParam(':url', $url);

		if($sessionID !== null)
		{
			$query->bindParam(':session', $sessionID);
		}

		$query->execute();
		$data = $query->fetchColumn();

		return @json_decode($data, true);
	}

	/**
	 * {@inheritdoc}
	 */
	public function invalidateCache($url = '')
	{
		$connection = static::getDatabase();
		if(empty($url))
		{
			$query = $connection->prepare("TRUNCATE TABLE " . static::$tableName);
		}
		else
		{
			$sessionID = $this->getSessionId();
			$query = $connection->prepare("DELETE FROM " . static::$tableName . " WHERE " . static::$sessionColumn . " = :session AND " . static::$urlColumn . " = :url");
			$query->bindParam(':session', $sessionID);
			$query->bindParam(':url', $url);
		}
		$query->execute();
	}

	/**
	 * @return PDO
	 */
	private static function getDatabase()
	{
		if(static::$connection === null)
		{
			static::$connection = new PDO('mysql:host=' . static::$host . ';dbname=' . static::$databaseName, static::$username, static::$password);
		}

		return static::$connection;
	}
}

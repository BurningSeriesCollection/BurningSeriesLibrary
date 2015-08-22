<?php

// Directory structure: {session_id}/{path}.json

class FileCacher extends BurningSeries
{
	/** @var string $path */
	private static $path = './cache/';
	/** @var int $cutoff */
	private static $cutoff = 3600; // One hour

	/**
	 * Set the path for caching
	 *
	 * @param string $path The path. Needs to be writeable and should be an absolute path to avoid issues
	 *
	 * @throws Exception
	 */
	public static function setPath($path)
	{
		if(substr($path, -1) != '/')
		{
			$path .= '/';
		}

		if(!is_dir($path))
		{
			@mkdir($path, 0777, true);
		}

		if(!is_dir($path) || !is_writeable($path))
		{
			throw new Exception("\$path needs to be a directory and writeable (Path: {$path})");
		}

		static::$path = $path;
	}

	/**
	 * Returns the current caching path
	 *
	 * @return string
	 */
	public static function getPath()
	{
		return static::$path;
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
		$data['bs_api_call'] = time();
		$data = json_encode($data);

		$file = $this->getFilename($url);
		$dir = dirname($file);

		if(!is_dir($dir))
		{
			@mkdir($dir, 0777, true);
		}

		$fp = @fopen($file, 'w');

		if($fp === false)
		{
			return;
		}

		fwrite($fp, $data);
		fclose($fp);
	}

	/**
	 * {@inheritdoc}
	 */
	protected function hasCache($url)
	{
		$file = $this->getFilename($url);

		if(!file_exists($file))
		{
			return false;
		}

		$data = @file_get_contents($file);

		if($data === false)
		{
			return false;
		}

		$data = @json_decode($data, true);

		if($data === false)
		{
			return false;
		}

		return ($data['bs_api_call'] > time() - static::$cutoff);
	}

	/**
	 * {@inheritdoc}
	 */
	protected function getCache($url)
	{
		$data = @json_decode(@file_get_contents($this->getFilename($url)), true);
		unset($data['bs_api_call']);
		return $data;
	}

	/**
	 * {@inheritdoc}
	 */
	public function invalidateCache($url = '')
	{
		if(empty($url))
		{
			static::clearCacheDir(static::$path);
		}
		else
		{
			unlink($this->getFilename($url));
		}
	}

	/**
	 * @param string $url
	 *
	 * @return string
	 */
	private function getFilename($url)
	{
		$path = static::$path;

		if($this->getSessionId() !== null)
		{
			$path .= $this->getSessionId() . '/';
		}

		return $path . $url . '.json';
	}

	/**
	 * Internal helper to delete the cache dir recursively
	 *
	 * @param string  $dir
	 * @param boolean $delete
	 */
	private static function clearCacheDir($dir, $delete = false)
	{
		if (is_dir($dir))
		{
			$objects = scandir($dir);
			foreach ($objects as $object)
			{
				if ($object != "." && $object != "..")
				{
					if (filetype($dir."/".$object) == "dir")
					{
						static::clearCacheDir($dir . "/" . $object, true);
					}
					else
					{
						unlink($dir . "/" . $object);
					}
				}
			}

			if($delete)
			{
				rmdir($dir);
			}
		}
	}
}

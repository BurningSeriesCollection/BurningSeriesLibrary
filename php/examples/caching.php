<?php

require_once '../src/BurningSeries.php';
require_once './Cacher/FileCacher.php';
require_once './Cacher/DBCacher.php';

// Default Class. Caches the calls in an internal static array.
$bs = new BurningSeries();

// File Cacher Class. Saves the calls in files which have a cutoff (default: 1 hour). Note that the path should be absolute to avoid issues
//$bs = new FileCacher();
//FileCacher::setPath(__DIR__ . '/cache/');

// Database Cacher Class. Same as the file cache but in a database
//$bs = new DBCacher();
//DBCacher::setDatabaseCredentials('username', 'password');

// Cache is enabled by default so normally there isn't a need to call that function
$bs->enableCache();

$bs->getSeries();
$bs->getNewest();
$bs->search('Naruto');
$bs->getByName('Dragonball');

$withCaching = $bs->getNumCalls();

$bs->disableCache();

$bs->getSeries();
$bs->getNewest();
$bs->search('Naruto');
$bs->getByName('Dragonball');

$withoutCaching = $bs->getNumCalls() - $withCaching;


echo "Without caching we'd have run {$withoutCaching} API calls. Caching reduces this to {$withCaching} calls.";

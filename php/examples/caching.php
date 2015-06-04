<?php

require_once '../src/BurningSeries.php';
$bs = new BurningSeries();

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

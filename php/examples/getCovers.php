<?php

// Serie can be the ID, the Name or if the series is already loaded the array
$serie = 'Dragonball';

require_once '../src/BurningSeries.php';
$bs = new BurningSeries();

$cover = $bs->getCover($serie);

echo "<img src=\"{$cover}\" alt=\"Cover for {$serie}\" />";

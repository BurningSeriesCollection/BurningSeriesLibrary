<?php

require_once '../src/class_bs.php';
$bs = new BurningSeries();

echo "<pre>";
var_dump(array_slice($bs->getNewest(), 0, 5));
echo "</pre>";

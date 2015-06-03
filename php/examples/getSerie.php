<?php

$serie = 3; // 24
if(!empty($_GET['s'])) {
	$serie = $_GET['s'];
}

require_once '../src/BurningSeries.php';
$bs = new BurningSeries();

$serie = $bs->getSerie($serie);

if(empty($serie)) {
	echo "No serie with ID {$id}";
	die();
}

echo "<h1>{$serie['series']}</h1>";

echo "Start: "; echo $serie['start'] != null ? $serie['start'] : '<i>Unknown</i>';
echo "<br /> End:"; echo $serie['end'] != null ? $serie['end'] : '<i>Unknown</i>';
echo "<br /> Has Movie(s): "; echo $serie['movies'] ? 'Yes' : 'No';
echo "<br /> Seasons: {$serie['seasons']}";
echo "<br /> {$serie['description']}";

//There's also the $serie['data'] array which has some usefull information like the producer or the genres

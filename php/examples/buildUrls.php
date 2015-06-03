<?php

// Serie can be the ID, the Name or if the series is already loaded the array
$serie = 3; // 24
$season = 1;
$episode = 1;

require_once '../src/BurningSeries.php';
$bs = new BurningSeries();

$serieUrl = $bs->buildSerieUrl($serie);
$seasonUrl = $bs->buildSeasonUrl($serie, $season);
$episodeUrl = $bs->buildEpisodeUrl($serie, $season, $episode);

// Or short:
// $serieUrl = $bs->buildUrl($serie);
// $seasonUrl = $bs->buildUrl($serie, $season);
// $episodeUrl = $bs->buildUrl($serie, $season, $episode);

echo "<a href=\"{$serieUrl}\" target=\"_blank\">Serie for {$serie} ({$serieUrl})</a> <br />";
echo "<a href=\"{$seasonUrl}\" target=\"_blank\">Season {$season} ({$seasonUrl})</a> <br />";
echo "<a href=\"{$episodeUrl}\" target=\"_blank\">Episode {$episode} ({$episodeUrl})</a>";

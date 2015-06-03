<?php

// You need to update these to get this working
$username = 'JonesH';
$password = '/"I!Pb0arddH-&2^\7#ic:k+{9v3Z1';

if(empty($username) || empty($password)) {
	echo 'Please update username and password in this file!';
}

require_once '../src/BurningSeries.php';
$bs = new BurningSeries($username, $password);

$favorites = $bs->getFavoriteSeries();

echo "The session key for this request is {$bs->getSessionId()} <br /><br />";

echo 'Your favorite series are: <br />';

foreach($favorites as $favorite) {
	echo "{$favorite['series']} (ID: {$favorite['id']}) <br />";
}

// Don't forget to logout if you're not using the sessionkey somewhere else!
$bs->logout();

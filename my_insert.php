<?php

header('Content-Type: application/json');

include_once("./db.php");

$sql = 'INSERT INTO `Order`
        VALUES (null, \'123qwe\', 1, \'SMALL\', \'ICE\', \'1 1 2\', 1904301850, 1, 4100 )';
$result = mysql_query($sql);

if(!$result){
    echo 'ERROR : ' .mysql_error();
    exit;
}

?>
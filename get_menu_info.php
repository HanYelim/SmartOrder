<?
    header('Content-Type: application/json');

    include_once("./db.php");

    $menu_name = $_GET['name'];

    $sql = 'SELECT * FROM Menu WHERE mname =' .$menu_name;
    $result = mysql_query($sql);        

    if(!$result){
        echo 'MySQL Error : ' . mysql_error();
        exit;
    }

    $ret = array();
    $index = 0;
    while($r = mysql_fetch_assoc($result)){
        $rows = array();
        $rows['index'] = $r['index'];
        $rows['name'] = $r['mname'];
        $rows['price'] = $r['mprice'];
        $rows['opt'] = $r['mopt'];
        $rows['type'] = $r['mtype'];
        $rows['size'] = $r['msize'];
        $ret[$index++] = $rows;
    }

    echo json_encode($ret);

?>
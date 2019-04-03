<?
    header('Content-Type: application/json');

    include_once("./db.php");

    $order_cus = $_GET['mCustomer'];
    $order_cus = "'123qwe'";

    $sql = 'SELECT DISTINCT m.index, m.mname, m.mprice, m.mopt, o.mtype, o.msize FROM `Order` AS o, `Menu` AS m WHERE o.mCustomer = ' .$order_cus. ' AND m.index = o.mMenu ORDER BY mIndex DESC LIMIT 3';
    $result = mysql_query($sql);

    if(!$result){
        echo 'ERROR : ' .mysql_error();
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
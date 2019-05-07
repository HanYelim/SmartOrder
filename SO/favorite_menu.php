<?
    header('Content-Type: application/json');

    include_once("./db.php");
    
    $order_cus = $_GET['mCustomer'];
    $order_cus = "'123qwe'";

    $sql = 'SELECT m.mname, o.mprice, o.msize, o.mtype, o.mmenu, count(o.mmenu) cnt FROM `Order` AS o, `Menu` AS m WHERE o.mCustomer = ' .$order_cus. ' and m.index = o.mMenu group by m.mname, o.mtype, o.msize Order By cnt Desc';
    $result = mysql_query($sql);

    if(!$result){
        echo 'ERROR : ' .mysql_error();
        exit;
    }
    
    $r = mysql_fetch_assoc($result);
    $row['name'] = $r['mname'];
    $row['price'] = $r['mprice'];
    $row['size'] = $r['msize'];
    $row['type'] = $r['mtype'];
    $row['index'] = $r['mmenu'];


    echo json_encode($row);

?>
<?php

    header('Content-Type: application/json');

    include_once("./db.php");

    $order_cus = $_POST["mCustomer"]; 
    $order_name = $_POST["name"];
    $order_msize = $_POST["size"];
    $order_mtype = $_POST["type"];
    $order_mopt = $_POST["option"];
    $order_mindex = $_POST["mindex"];
    $order_mcount = $_POST["count"];
    $order_price = $_POST["price"];
    
    $sql_1 = 'SELECT m.index FROM Menu as m WHERE m.mname = \'' .$order_name. '\''; 
    $result = mysql_query($sql_1);
    $r = mysql_fetch_assoc($result);
    $order_menu = $r['index'];

    echo $order_cus;
    echo $order_menu;
    echo $order_msize;
    echo $order_mtype;
    echo $order_mopt;
    echo $order_mindex;
    echo $order_mcount;
    echo $order_price;

    // $sql = 'INSERT INTO `Order`
    //         VALUES ( null, \'' .$order_cus. '\', ' .$order_menu. ', \'' .$order_msize. '\', \'' .$order_mtype. '\', \'' .$order_mopt. 
    //         '\', ' .$order_mindex. ', ' .$order_mcount. ', ' .$order_price. ')';
            
    $sql = 'INSERT INTO `Order`
    VALUES ( null, \'' .$order_cus. '\', ' .$order_menu. ', \'' .$order_msize. '\', \'' .$order_mtype. '\', \'' .$order_mopt. 
    '\', ' .$order_mindex. ', ' .$order_mcount. ', ' .$order_price. ')';
    $result = mysql_query($sql);
    
    if(!$result){
        echo 'ERROR : ' .mysql_error();
        exit;
    }

?>
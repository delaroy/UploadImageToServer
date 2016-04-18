<?php
if(isset($_POST['ImageName'])){
$imgname = $_POST['ImageName'];
$imsrc = base64_decode($_POST['base64']);
$fp = fopen($imgname, 'w');
fwrite($fp, $imsrc);
    if(fclose($fp)){
        echo $imgname. " has been uploaded successfully.";
                //Deleting file after file has been upload.
        //unlink($imgname);
    }else{
        echo "Sorry, there was an error uploading your file.";
    }
}
?>
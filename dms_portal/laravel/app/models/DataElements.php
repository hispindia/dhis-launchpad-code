<?php
/**
 * Created by PhpStorm.
 * User: gaurav
 * Date: 17/5/14
 * Time: 2:54 PM
 */

class DataElements extends Eloquent{

    /*---Custom table table to connect with the model---*/
    protected $table = 'dataelement';

    /*---Primary Key---*/
    protected $id = 'dataelementid';

    /*---Skip force adding timestamp rows---*/
    public $timestamps = false;

} 
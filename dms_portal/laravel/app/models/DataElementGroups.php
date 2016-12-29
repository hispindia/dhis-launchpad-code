<?php
/**
 * Created by PhpStorm.
 * User: gaurav
 * Date: 17/5/14
 * Time: 2:53 PM
 */

class DataElementGroups extends Eloquent{

    /*---Custom table table to connect with the model---*/
    protected $table = 'dataelementgroup';

    /*---Primary Key---*/
    protected $id = 'dataelementgroupid';

    /*---Skip force adding timestamp rows---*/
    public $timestamps = false;

    /*---Get member dataelements---*/
    public function dataelements()
    {
        return $this->belongsToMany('DataElementGroups','dataelementgroupsetmembers',
            'dataelementgroupsetid','dataelementgroupid');
    }

} 
<?php
/**
 * Created by PhpStorm.
 * User: Gaurav
 * Date: 17/5/14
 * Time: 11:21 AM
 */

class DataElementGroupSets  extends Eloquent{

    /*---Custom table table to connect with the model---*/
    protected $table = 'dataelementgroupset';

    /*---Primary Key---*/
    protected $id = 'dataelementgroupsetid';

    /*---Skip force adding timestamp rows---*/
    public $timestamps = false;

    /*---Get member groups---*/
    public function groups()
    {
        return $this->belongsToMany('DataElementGroups','dataelementgroupsetmembers',
            'dataelementgroupsetid','dataelementgroupid');
    }

} 
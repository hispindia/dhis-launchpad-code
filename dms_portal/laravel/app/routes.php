<?php

use Illuminate\Database\Eloquent\ModelNotFoundException;

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the Closure to execute when that URI is requested.
|
*/

Route::get('/', function () {
    return View::make('hello');
});

Route::get('/groupSets', function () {

    /** @var $dataelementgroupsets "dataelementgroupset" database objects */

    $dataelementgroupsetsmembers = DataElementGroupSets::all();

    return Response::json($dataelementgroupsetsmembers)->setCallback(Input::get('callback'));
});

Route::get('/groupSetNames', function () {

    /** @var $dataelementgroupsets "dataelementgroupset" database objects */

    $dataelementgroupsetsmembers = DataElementGroupSets::all();

    return Response::json($dataelementgroupsetsmembers)->setCallback(Input::get('callback'));
});

Route::get('/dataElements', function () {

    /** @var $dataelementgroupsets "dataelementgroupset" database objects */

    $dataelements = DataElements::all();

    return Response::json($dataelements)->setCallback(Input::get('callback'));
});

Route::get('/getGroups', 'QueryController@getGroups');

Route::get('/getFilterLocations', 'QueryController@getFilterLocations');

Route::get('/getItemList', 'QueryController@getItemList');

Route::get('/getItemListTest', 'QueryController@getItemListTest');

Route::get('/getDataLocations', 'QueryController@getDataLocations');

Route::get('/getDateRange', 'QueryController@getDateRange');

Route::get('/getData', 'QueryController@getData');

Route::get('/getSubGroups', 'QueryController@getSubGroups');

Route::get('/getFrequencyList', 'QueryController@getFrequencyList');




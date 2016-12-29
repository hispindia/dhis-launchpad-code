    var ExcelCellMap = new Object();

    function lockScreen(){
        jQuery.blockUI({ message: 'Please wait' , css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff'
        }
        });
    }

    function unLockScreen(){
        jQuery.unblockUI();
    }

    function importData(sheetName){
        // import data
        console.log('sheet Name '+ sheetName);

        var url = 'importData.action';

        $( '#contentDiv' ).load( url, function(responseTxt, statusTxt, xhr)
        {
             if(statusTxt == "success")
                    console.log("ajax call return successfully!");
                if(statusTxt == "error")
                    console.log("Error: " + xhr.status + ": " + xhr.statusText);
        });
    }

    function importExcelData(trackedEntityInstanceObj, enrollmentObj, eventObj){
        var successCount = 0,
            errorCount = 0;

        var failedReasonMap = new Object();

        var trackedEntityInstancesIds = [];

       /* console.log('registration object '+ JSON.stringify(trackedEntityInstanceObj));
        console.log('enrollment object '+ JSON.stringify(enrollmentObj));
        console.log('event object '+ JSON.stringify(eventObj));*/

        $.ajax( {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: '../api/trackedEntityInstances',
            data: JSON.stringify(trackedEntityInstanceObj),
            dataType: 'json',
            type: 'post',
            success: handleRegistrationSuccess,
            error: handleRegistrationError
        } );

        function handleRegistrationSuccess(data)
        {
            //registration success go for enrollment
            var trackedEntityInstance='';

            if(data.conflicts != undefined){ //when duplicate occurs conflict occurs
                var errorValue =  data.conflicts[0].value;
                failedReasonMap[errorCount++] = errorValue ;

                console.log(errorValue);
            }
            else if(data.reference != undefined){ // for unique entry

                successCount++;
                trackedEntityInstance = data.reference;
                console.log('Person Registered successfully! with tracked entity instance id: '+trackedEntityInstance);
                trackedEntityInstancesIds.push(trackedEntityInstance);

                enrollmentObj.trackedEntityInstance = trackedEntityInstance;

                $.ajax( {
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    url: '../api/enrollments',
                    data: JSON.stringify(enrollmentObj),
                    dataType: 'json',
                    type: 'post',
                    success: handleEnrollmentSuccess,
                    error: handleEnrollmentError
                } );
            }

            function handleEnrollmentSuccess(data)
            {
             //enrollment success go for event

                var enrollmentId='';

                if(data.conflicts != undefined){ //when duplicate occurs conflict occurs
                    var errorValue =  data.conflicts[0].value;
                    console.log(errorValue);
                }
                else if(data.reference != undefined){ // for unique entry

                    enrollmentId = data.reference;
                    console.log('Person Enrolled successfully! with enrollment id: '+enrollmentId);

                    eventObj.trackedEntityInstance = trackedEntityInstance ;

                    $.ajax( {
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        },
                        url: '../api/events',
                        data: JSON.stringify(eventObj),
                        dataType: 'json',
                        type: 'post',
                        success: handleEventSuccess,
                        error: handleEventError
                    } );
//                unLockScreen();
                }
            }

            function handleEventSuccess(data)
            {
               console.log('Event created successfully!')
            }

            function handleEventError(textStatus,errorThrown)
            {
                console.log('Creating event Error '+textStatus);
                console.log(errorThrown);
            }

            function handleEnrollmentError(textStatus,errorThrown)
            {
                console.log('Enrollment Error '+textStatus);
                console.log(errorThrown);
            }
        }
        function handleRegistrationError( textStatus, errorThrown )
        {
            console.log('Registration Error'+textStatus);
            console.log(errorThrown);
        }

    }

    function to_Map(workbook) {
        workbook.SheetNames.forEach(function(sheetName) {
            ExcelCellMap = XLSX.utils.sheet_to_formulae(workbook.Sheets[sheetName]);
            //   console.log('excel map '+ ExcelCellMap);

            if(ExcelCellMap != undefined){
                importData(sheetName);
            }
        });
    }

    function fixdata(data) {
        var o = "", l = 0, w = 10240;
        for(; l<data.byteLength/w; ++l) o+=String.fromCharCode.apply(null,new Uint8Array(data.slice(l*w,l*w+w)));
        o+=String.fromCharCode.apply(null, new Uint8Array(data.slice(l*w)));
        return o;
    }

    function xlsxworker(data) {
        var rABS = true;
        var worker = new Worker('/dhis-web-dataentry-import/javascript/xlsxworker.js');
        worker.onmessage = function(e) {
            switch(e.data.t) {
            case 'ready': break;
            case 'e': console.error(e.data.d); break;
            default : to_Map(JSON.parse(e.data.d)); break;
            }
        };
        var arr = rABS ? data : btoa(fixdata(data));
        worker.postMessage({d:arr,b:rABS});
    }

    function uploadFile(){
        var file = document.getElementById('fileInput').files[0];

        var filename = $('#fileInput').val().split('\\').pop();
        var extension = filename.split('.').pop();

        if (!file) {
            alert("Error Cannot find the file!");
            return;
        }
        else if(extension.toLowerCase() != 'xlsx'){
                    alert('select file with extension .xlsx only!');
                    return;
                }

       var rABS = true;
       var   use_worker = true;

        var reader = new FileReader();
        reader.onload = function(e) {
            var data = e.target.result;
            if(use_worker) {
                xlsxworker(data);
            } else {
                var wb;
                if(rABS) {
                    wb = XLSX.read(data, {type: 'binary'});
                } else {
                    var arr = fixdata(data);
                    wb = XLSX.read(btoa(arr), {type: 'base64'});
                }
            }
        };
        if(rABS) reader.readAsBinaryString(file);
        else reader.readAsArrayBuffer(file);
    }

    function cancel(){
        //alert('hello');
    }
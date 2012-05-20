var request = null;  // we want this to be global

function sendTask(button) {
    var rowNum = button.name.substring(5);
    var form = document.getElementById('taskList').parentNode;
    var description = form.elements['description' + rowNum].value;
    var formHTML = "<input name='row' value='" + rowNum + "'/>";
    formHTML += "<input name='description' value='" + description + "'/>";
    formHTML += "<input name='command' value='" + button.value + "'/>";
    var completed = form.elements['completed' + rowNum];
    if (completed.checked)
	    formHTML += "<input name='completed' value='on'/>";
    
	form = form.cloneNode(false);
	form.innerHTML = formHTML;
	
    return getTaskList(form);
}

function getTaskList(form) {
	var params = "?dummy=" + new Date().getTime();
	if (form) {
		for (var i = 0; i < form.elements.length; ++i) {
			if (form.elements[i].type !== 'checkbox' || form.elements[i].checked) {
				params += "&" + form.elements[i].name + "=" + escape(form.elements[i].value);
			}
		}
	}
	sendRequest('toDoManager.do' + params, updateTaskList);
	
	return false;
}

function updateTaskList() {
	if (request.readyState == 4) {
		if (request.status == 200) {
			var response = request.responseText;
			var tasksDiv = document.getElementById('tasksDiv');
			tasksDiv.innerHTML = response;
		} else {
			alert("There was an error processing your request.  Request status is " + request.status);
		}
	}
}

function sendRequest(url, handler) {
	getXMLHttpRequest();
	
	request.open('GET', url);
	request.onreadystatechange = handler;
	request.send(null);
}

function getXMLHttpRequest( ) {
	try {
		request = new XMLHttpRequest();
	}catch(err1) {
		try {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}catch(err2) {
			try {
				request = new ActiveXObject("Msxml2.XMLHTTP");
			}catch(err3) {
				request = null;
			}
		}
	}
	if (request == null) alert("Error creating request object!");
}

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Building microservices with Java EE 8 and Microprofile APIs</title>

    <link href="css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>

<div class="container theme-showcase" role="main">

    <div class="jumbotron">
        <h1>Java EE 8 Weather Station</h1>
    </div>

    <div class="row">
        <div class="col-md-6">
            <form action="" method="post" onsubmit="submitForm(); return false;">
                <div class="form-group">
                    <label for="city">City</label>
                    <input type="text" class="form-control" id="city" placeholder="Munich,de">
                </div>
                <button type="submit" class="btn btn-primary">Submit</button>
            </form>
            <script>
                function submitForm() {
                    var urlEncodedData;
                    var urlEncodedDataPairs = [];
                    var http = new XMLHttpRequest();

                    http.open("POST", "/api/weather", true);
                    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

                    var city = document.getElementById('city').value;
                    document.getElementById('city').value = "";

                    urlEncodedDataPairs.push('city' + '=' + encodeURIComponent(city));

                    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
                    http.send(urlEncodedData);
                }
            </script>
        </div>
    </div>

    <div class="row">
        <div class="col-md-6"></div>
    </div>

    <div class="row">
        <div class="col-md-6">
            <h3>Current Weather Events</h3>
        </div>
    </div>

    <div class="row">
        <div id="events" class="col-md-6">

            <script>
                if (typeof(EventSource) !== "undefined") {
                    var source = new EventSource("/api/weather-station");

                    source.addEventListener("event", function (e) {
                        document.getElementById("events").innerHTML += "Current weather (SSE) in " + e.data + "<br>";
                    }, false);
                } else {
                    document.getElementById("events").innerHTML = "Sorry, your browser does not support server-sent events...";
                }
            </script>
        </div>
    </div>
</div>

</body>
</html>
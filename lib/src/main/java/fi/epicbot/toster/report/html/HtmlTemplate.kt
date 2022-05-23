package fi.epicbot.toster.report.html

import fi.epicbot.toster.report.html.BaseHtmlReporter.Companion.CHART_VERSION

internal val MAIN_TEMPLATE = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>toster report</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>

<body>

<h1>@@app_name@@</h1>

<h2>Devices</h2>

@@devices@@

@@generated_with@@
</body>
</html>
""".trimIndent()

internal val DEVICE_TEMPLATE = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>toster report</title>
    <link rel="stylesheet" type="text/css" href="../styles.css">
</head>

<body>

<h1><a href="../index.html">@@app_name@@</a> / @@device_name@@</h1>

<h2>Metrics</h2>

<div>
    <ul>
        <li><a href="cpu/index.html">CPU</a></li>
        <li><a href="memory/index.html">Memory</a></li>
        <li><a href="collage/index.html">Collage</a></li>
    </ul>
</div>

@@generated_with@@
</body>
</html>
""".trimIndent()

internal val COLLAGE_TEMPLATE = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>toster report</title>
    <link rel="stylesheet" type="text/css" href="../../styles.css">
</head>

<body>

<h1><a href="../../index.html">@@app_name@@</a> / <a href="../index.html">@@device_name@@</a> / Collage</h1>

<div class="grid-container">
@@collage@@
</div>

@@generated_with@@
</body>
</html>
""".trimIndent()

internal val STYLE_TEMPLATE = """
h2 {
  background-color: #666666;
  padding: 0.2em;
  color: #ffffff;
}

a:link, a:hover, a:visited {
  color: blue;
}

.grid-container {
  display: grid;
  grid-template-columns: auto auto auto auto;
  padding: 10px;
}
.grid-item {
  border: 1px solid rgba(0, 0, 0, 0.8);
  padding: 20px;
  font-size: 30px;
  text-align: center;
}
""".trimIndent()

internal val CPU_TEMPLATE = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>toster report</title>
    <link rel="stylesheet" type="text/css" href="../../styles.css">
    <script src="cpu_data.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/$CHART_VERSION/chart.js"></script>
</head>

<body>

<h1><a href="../../index.html">@@app_name@@</a> / <a href="../index.html">@@device_name@@</a> / CPU</h1>

@@metrics@@

@@generated_with@@
<script type="text/javascript" src="chart_builder.js"></script>
</body>
</html>
""".trimIndent()

internal val MEMORY_TEMPLATE = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>toster report</title>
    <link rel="stylesheet" type="text/css" href="../../styles.css">
    <script src="memory_data.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/$CHART_VERSION/chart.js"></script>
</head>

<body>

<h1><a href="../../index.html">@@app_name@@</a> / <a href="../index.html">@@device_name@@</a> / Memory</h1>

@@metrics@@

@@generated_with@@
<script type="text/javascript" src="chart_builder.js"></script>
</body>
</html>
""".trimIndent()

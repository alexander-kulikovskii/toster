package fi.epicbot.toster.report.html

import fi.epicbot.toster.report.html.BaseHtmlReporter.Companion.LIB_VERSION

internal val START_PAGE = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>toster report</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>

<body>

<h1>app</h1>

<h2>Devices</h2>

<div>
  <ul>
    <li><a href="test_name/index.html">test_name</a></li>
  </ul>
</div>


<div>generated with <a href="https://github.com/alexander-kulikovskii/toster">toster version $LIB_VERSION</a></div>

</body>
</html>
""".trimIndent()

internal val STYLES_FILE = """
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

internal val DEVICE_PAGE = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>toster report</title>
    <link rel="stylesheet" type="text/css" href="../styles.css">
</head>

<body>

<h1><a href="../index.html">app</a> / test_name</h1>

<h2>Metrics</h2>

<div>
    <ul>
        <li><a href="cpu/index.html">CPU</a></li>
        <li><a href="memory/index.html">Memory</a></li>
        <li><a href="collage/index.html">Collage</a></li>
    </ul>
</div>

<div>generated with <a href="https://github.com/alexander-kulikovskii/toster">toster version $LIB_VERSION</a></div>

</body>
</html>
""".trimIndent()

package fi.epicbot.toster.report.html

import fi.epicbot.toster.report.html.BaseHtmlReporter.Companion.LIB_VERSION

internal val COLLAGE_REPORT = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>toster report</title>
    <link rel="stylesheet" type="text/css" href="../../styles.css">
</head>

<body>

<h1><a href="../../index.html">app</a> / <a href="../index.html">test_name</a> / Collage</h1>

<div class="grid-container">
<div>
  <h3>common 2</h3>
  <h4>test</h4>
<img src="../../../test/234" height="500"></div>

</div>

<div>generated with <a href="https://github.com/alexander-kulikovskii/toster">toster version $LIB_VERSION</a></div>

</body>
</html>
""".trimIndent()

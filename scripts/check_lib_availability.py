import argparse
import sys
import io
import time
import os
import shutil
import fileinput


RAW_PROJECT_PATH = "raw_checker/"
TMP_PROJECT_PATH = "tmp_checker/"
APP_GRADLE_PATH = TMP_PROJECT_PATH + "app/build.gradle.kts"
GENERAL_GRADLE_PATH = TMP_PROJECT_PATH + "build.gradle.kts"
GRADLE_WRAPPER_PROPERTIES_PATH = TMP_PROJECT_PATH + "gradle/wrapper/gradle-wrapper.properties"


def _copy_tmp_project():
    if os.path.exists(TMP_PROJECT_PATH):
        shutil.rmtree(TMP_PROJECT_PATH)
    shutil.copytree(RAW_PROJECT_PATH, TMP_PROJECT_PATH)


def _replace_variables(file_path, name, value):
    with open(file_path) as f:
        new_file_content = f.read().replace(name, value)
    with open(file_path, "w") as f:
        f.write(new_file_content)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("-v", "--version", dest="version", required=True)
    parser.add_argument("-p", "--plugin", dest="gradle_plugin", required=True)
    parser.add_argument("-t", "--tools", dest="gradle_tools", required=True)
    parser.add_argument("-k", "--kotlin", dest="kotlin_version", required=True)
    args = parser.parse_args()

    _copy_tmp_project()
    _replace_variables(APP_GRADLE_PATH, '$$VERSION$$', args.version)
    _replace_variables(GRADLE_WRAPPER_PROPERTIES_PATH, '$$GRADLE_PLUGIN$$', args.gradle_plugin)
    _replace_variables(GENERAL_GRADLE_PATH, '$$GRADLE_TOOLS$$', args.gradle_tools)
    _replace_variables(GENERAL_GRADLE_PATH, '$$KOTLIN_VERSION$$', args.kotlin_version)
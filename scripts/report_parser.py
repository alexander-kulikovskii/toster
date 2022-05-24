import argparse
import requests
import sys
import re
import xml.etree.ElementTree as ET

PITEST_FILE_PATH = "lib/build/reports/pitest/mutations.xml"
CODECOV_FILE_PATH = "lib/build/reports/kover/project-xml/report.xml"
BASE_URL = "https://us-central1-epicbot-github-badges.cloudfunctions.net"


def _parse_pitest_coverage():
    data = open(PITEST_FILE_PATH).read()
    data = re.sub(r'&#([a-zA-Z0-9]+);?', r'[#\1;]', data)
    root = ET.fromstring(data)
    survived_count = 0
    killed_count = 0
    for mutation in root:
        if mutation.attrib['detected'] == 'true':
            killed_count += 1
        else:
            survived_count += 1

    return round((killed_count / (survived_count + killed_count)) * 100.0, 2)


def _parse_code_coverage():
    root = ET.parse(CODECOV_FILE_PATH).getroot()
    for mutation in root:
        if 'type' in mutation.attrib and mutation.attrib['type'] == 'LINE':
            covered = int(mutation.attrib['covered'])
            missed = int(mutation.attrib['missed'])
            return round((covered / (covered + missed)) * 100.0, 2)

    sys.exit("Can't parse code coverage")


def _send_update_status(key, value, project_id, project_token):
    print("Send calculated value %s for %s property..." % (value, key))
    data = {
        key: value,
        "projectToken": project_token
    }
    url = "%s/project/%s/update" % (BASE_URL, project_id)
    response = requests.post(url, json=data)
    print(response)
    if response.status_code != 200:
        sys.exit(response)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("-i", "--id", dest="project_id", required=True)
    parser.add_argument("-t", "--token", dest="project_token", required=True)
    parser.add_argument("-k", "--key", dest="key", required=True)
    args = parser.parse_args()

    parser_map = {'pitestCoverage': _parse_pitest_coverage, 'codeCoverage': _parse_code_coverage}

    if args.key not in parser_map:
        sys.exit("Can't parse it")

    value = parser_map[args.key]()
    _send_update_status(args.key, value, args.project_id, args.project_token)

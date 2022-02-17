pushd tmp_checker
if ./gradlew check; then
  echo Done
else
  exit 1
fi
popd
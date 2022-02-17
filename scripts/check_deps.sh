cd tmp_checker/
./gradlew :app:dependencies
if ./gradlew :app:check; then
  echo Done
else
  exit 1
fi

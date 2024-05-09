#!/usr/bin/env bash

sbt clean scalastyle coverage IntegrationTest/test coverageReport dependencyUpdates

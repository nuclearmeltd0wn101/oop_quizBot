#!/bin/bash

cat ??_init_*.sql | sqlite3 $1


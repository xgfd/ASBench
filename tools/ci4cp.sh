self=$0
sh execQ.sh ./queries/${self%%.*}.rq ${1?Two class names are required} ${2?Two class names are required} ${3:-$1} ${4:-$2}

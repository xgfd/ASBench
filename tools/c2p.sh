self=$0
sh execQ.sh ./queries/${self%%.*}.rq ${1?Two class names are required} ${2?Two class names are required}
# classA=dbo:$1
# classB=dbo:$2
#
# query="CONSTRUCT+{${classA}+?p+${classB}}+WHERE+{+[]+a+${classA};+?p+[+a+${classB}].+}"
#
# echo \#${query}
#
# curl -g "http://dbpedia.org/sparql?query=${query}&timeout=120000"

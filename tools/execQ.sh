# Execute a query specified by a template that is filled by command arguments
tempt=${1?"Usage: $0 template [template arguments]"}

# consume the first arg,
# rest args are passed to the query template
shift

query=""
while read line
do
    line=$(echo $line | sed 's/\([;(*)]\)/\\\1/g') # escape ; ( ) *
    # echo $line
    query+=$(eval echo "$line") # escape command separator ;
    query+="\n"
done < $tempt

# echo $query

curl -G "http://dbpedia.org/sparql?timeout=300000" --data-urlencode "query=${query//'\n'/ }" --data-urlencode "default-graph-uri=http://dbpedia.org" #-H 'Accept: text/csv' #replace newline by a space

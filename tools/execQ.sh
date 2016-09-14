# parse optional arguments

# Use -gt 1 to consume two arguments per pass in the loop (e.g. each
# argument has a corresponding value to go with it).
# Use -gt 0 to consume one or more arguments per pass in the loop (e.g.
# some arguments don't have a corresponding value to go with it such
# as in the --default example).

arguments=""

while [[ $# -gt 0 ]]
do
    key="$1"

    case $key in
        -G|--get)
        # echo 'G'
        endpoint="$2"
        ;;
        # -h|--header)
        # # echo 'h'
        # header="$2"
        # ;;
        # -g|--default)
        # echo 'g'
        # g="$2"
        # ;;

        -*)
        # all other parameters
        param="$1"
        val="$2"
        arguments="$arguments $param \"$val\""
        ;;

        *)
        # no more arguments
        break
        ;;
    esac
    shift # past argument
    shift # past argument value
done

: ${endpoint:="http://dbpedia.org/sparql?timeout=300000"}
# : ${header:="Accept: application/sparql-results+json"}
# : ${g:="default-graph-uri=http://dbpedia.org"}

# execute a query specified by a template that is filled by command arguments
template=${1?"Usage: $0 template [template arguments]"}

# consume the first arg,
# rest args are passed to the query template
shift

query=""
while IFS= read -r line || [ -n "$line" ]; # http://stackoverflow.com/a/31398490/2399278
do
    line=$(echo $line | sed 's/\([;(<*>)]\)/\\\1/g') # escape ; ( ) *
    # echo $line
    query+=$(eval echo "$line") # escape command separator ;
    query+="\n"
done < $template

# echo $query

eval "curl  -G \"$endpoint\" --data-urlencode \"query=${query//'\n'/ }\" $arguments" #replace newline by a space

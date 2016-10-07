# compute the cardinality of predicates given in a file
declare -a predicates
url=${1?SPARQL endpoint is required}
predicates=(`cat ${2?Require a file of predicates}`)

for ((j = 0; j < ${#predicates[@]}; j++))
do
    p=${predicates[$j]}
    echo "Processing predicate: ${p}"
    sh p_card.sh $url ${p} >> card.csv
    printf '\n\n' >> card.csv
done
echo Finished!

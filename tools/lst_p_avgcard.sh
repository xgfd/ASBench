# compute the cardinality of predicates given in a file
declare -a predicates
predicates=(`cat ${1?Require a file of predicates}`)

for ((j = 0; j < ${#predicates[@]}; j++))
do
    p=${predicates[$j]}
    echo "Processing predicate: ${p}"
    sh p_avgcard.sh ${p} >> p_avgcard.csv
    printf '\n\n' >> p_avgcard.csv
done
echo Finished!

# compute the cardinality of predicates given in a file
declare -a predicates
predicates=(`cat ${1?Require a file of predicates}`)

for ((j = 0; j < ${#predicates[@]}; j++))
do
    p=${predicates[$j]}
    echo "Processing predicate: ${p}"
    sh p_inverseCard.sh ${p} >> inverseCard.csv
    printf '\n\n' >> inverseCard.csv
done
echo Finished!

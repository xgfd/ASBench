declare -a predicates
predicates=(`cat ${1?Require a file of predicates}`)

for ((j = 0; j < ${#predicates[@]}; j++))
do
    p=${predicates[$j]}
    echo "Processing predicate: ${p}"
    sh p_card.sh ${p} >> p_card.csv
    printf '\n\n' >> p_card.csv
done
echo Finished!

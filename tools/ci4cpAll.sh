declare -a classNames
classNames=(`cat ${1?Require a file of class names}`)

index=0

for ((i = 0; i < ${#classNames[@]}; i++))
    do
        for ((j = 0; j < ${#classNames[@]}; j++))
            do
                classA=${classNames[$i]}
                classB=${classNames[$j]}
                echo "Processing pair: ${classA} - ${classB}; index $index"
                sh ci4cp.sh ${classA} ${classB} $classA$index $classB$index >> ci4cp.ttl
                printf '\n\n' >> ci4cp.ttl
                ((index++))
            done
    done
echo Finished!

declare -a classNames
classNames=(`cat ${1?Require a file of class names}`)

for ((i = 0; i < ${#classNames[@]}; i++))
    do
        for ((j = 0; j < ${#classNames[@]}; j++))
            do
                classA=${classNames[$i]}
                classB=${classNames[$j]}
                echo "Processing pair: ${classA} - ${classB}"
                sh c2p.sh ${classA} ${classB} >> class_properties.ttl
                printf '\n\n' >> class_properties.ttl
            done
    done
echo Finished!

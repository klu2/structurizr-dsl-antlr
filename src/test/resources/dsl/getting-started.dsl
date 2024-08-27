/*
 * some comment
 */
workspace {

    model {
        user = person "User"
        softwareSystem1 = softwareSystem "Software System"

        user -> softwareSystem1 "Uses"
    }

    views {
        systemContext softwareSystem1 {
            include *
            autolayout
        }
    }
    
}
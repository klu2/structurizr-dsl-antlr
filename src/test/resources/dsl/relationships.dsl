workspace {

    model {
        softwareSystem1 = softwareSystem "Software System"
        person User {
            -> softwareSystem1 "Uses"
        }
        person "User1" {
            this -> softwareSystem1 "Uses"
        }
    }
}
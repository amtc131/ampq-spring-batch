package data

type Consumer struct {
	ID        int64  `json:"id"`
	FirstName string `json:"firstName"`
	LastName  string `json:"lastName"`
	Birthdate string `json:"birthdate"`
}

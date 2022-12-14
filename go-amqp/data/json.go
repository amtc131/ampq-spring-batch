package data

import (
	"encoding/json"
	"io"
)

func ToJSON(i interface{}, w io.Writer) error {
	e := json.NewEncoder(w)
	return e.Encode(i)
}

func FromJSON(i interface{}, r io.Reader) error {
	d := json.NewDecoder(r)
	return d.Decode(i)
}

func Marshal(i interface{}) ([]byte, error) {
	return json.Marshal(i)
}

func Unmarshal(b []byte, i interface{}) {
	json.Unmarshal(b, &i)
}

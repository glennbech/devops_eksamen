provider "google" {
  credentials = "${file("google-key.json")}"
  project     = "devopsexam-295612"
  region      = "us-central1"
  zone        = "us-central1-c"
}
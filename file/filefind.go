package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"path/filepath"
	"strings"
)

var match = 0
var ch = make(chan int, 100)

//用于文档查找关键字
func FindFile(path string, name string) {
	count := 0
	var pathgroup [50]string
	filepath.Walk(path, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			fmt.Printf("path error")
		}
		if !info.IsDir() {
			if info.Size() < 1024*1024 {
				pathgroup[count] = path

				count++
				//fmt.Println(path, count)
				if count >= 50 {
					count = 0
					go Findname(pathgroup[0:50], name)
					<-ch
				}
			}
		}
		return nil
	})
	go Findname(pathgroup[0:count], name)
	<-ch

}

func Findname(paths []string, name string) {
	for _, path := range paths {
		filehca, err := os.Open(path)
		if err != nil {
			fmt.Printf(" path error")
		}
		defer filehca.Close()
		fd, _ := ioutil.ReadAll(filehca)
		if strings.Index(string(fd), name) > -1 {
			match++
			fmt.Println(path)
		}
	}
	ch <- 1
	fmt.Printf("filenum:%d\n", match)
}

//用于查找相关文件
func SelectFile(path string, name string) {
	count := 0
	var pathgroup [50]string
	filepath.Walk(path, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			fmt.Printf("path error")
		}
		if !info.IsDir() {
			if info.Size() < 1024*1024 {
				pathgroup[count] = path
				flag := strings.Contains(path, name)
				if flag {
					count++
					fmt.Println(path)
				}
				//fmt.Println(path, count)
			}
		}
		return nil
	})

}
func main() {
	// args := os.Args
	// if len(args) < 3 {
	// 	fmt.Println("path name")
	// 	return
	// }
	// if len(args) > 3 {
	// 	fmt.Println("path name")
	// 	return
	// }
	// path := args[1]
	// text := args[2]

	var path string
	var name string
	path = `F:\soft`
	name = "test"

	_, err := os.Stat(path)

	fmt.Println(path, name)
	if err != nil {
		fmt.Println("path error")
	} else {
		FindFile(path, name)
		SelectFile(path, name)
	}
}

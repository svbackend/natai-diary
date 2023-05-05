import DiaryLayout from "../../../src/modules/diary/components/DiaryLayout";
import {fetchPostLinks, fetchPostLinksLoad, useGetCategories} from "../../../src/api/apiComponents";
import {useState} from "react";
import {AlertApiError} from "../../../src/modules/common/components/alert";

const uniqueArray = (array: any[]) => {
    return array.filter((value, index) => array.indexOf(value) === index);
}

export default function NewLinkPage() {
    const {data} = useGetCategories({});

    const [categories, setCategories] = useState<number[]>([])

    const addCategory = (id: number) => {
        setCategories([...categories, id])
    }

    const removeCategory = (id: number) => {
        setCategories(categories.filter((category) => category !== id))
    }

    const categoryById = (id: number) => {
        return data?.categories.find((cat) => cat.id === id)
    }

    const [title, setTitle] = useState<string>("")
    const [url, setUrl] = useState<string>("")
    const [description, setDescription] = useState<string>("")
    const [image, setImage] = useState<string | null>(null)

    const [loading, setLoading] = useState<boolean>(false)

    const [error, setError] = useState(null)

    const send = () => {
        if (loading) return;
        if (!categories.length) return;

        setLoading(true);
        setError(null);

        fetchPostLinks({
            body: {
                title: title,
                url: url,
                description: description,
                image: image,
                categories: uniqueArray(categories)
            }
        }).then((res) => {
            alert("Link created");
        }).catch(e => {
            alert("Error creating link");
            setError(e);
        }).finally(() => {
            setLoading(false);
        })
    }

    // load title, description, og:image from url
    const loadInfo = async () => {
        const loadLinkResponse = await fetchPostLinksLoad({
            body: {
                url: url
            }
        })
        const html = loadLinkResponse.html;
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, "text/html");
        processDoc(doc);
    }

    const processDoc = (doc: Document) => {
        const title = doc.querySelector("title")?.textContent;
        const description = doc.querySelector("meta[name='description']")?.getAttribute("content");
        const image = doc.querySelector("meta[property='og:image']")?.getAttribute("content");
        if (title) setTitle(title);
        if (description) setDescription(description);
        if (image) setImage(image);
    }

    return (
        <DiaryLayout>
            <h1>New Link</h1>

            {error && (
                <AlertApiError error={error}/>
            )}

            <h3>Categories</h3>
            <div className={"flex flex-row flex-wrap"}>
                {data?.categories.map((cat) => (
                    <div key={cat.id} className={"p-2"} onClick={() => addCategory(cat.id)}>
                        {cat.name}
                    </div>
                ))}
            </div>

            <h3>Selected Categories</h3>
            <div className={"flex flex-row flex-wrap"}>
                {uniqueArray(categories).map((cat) => (
                    <div key={cat} className={"p-2"} onClick={() => removeCategory(cat)}>
                        {categoryById(cat)?.name}
                    </div>
                ))}
            </div>

            <h3>Url</h3>
            <input type={"text"} value={url} onChange={(e) => setUrl(e.target.value)}/>

            <button onClick={loadInfo}>Load</button>

            <h3>Title</h3>
            <input type={"text"} value={title} onChange={(e) => setTitle(e.target.value)}/>

            <h3>Description</h3>
            <textarea value={description} onChange={(e) => setDescription(e.target.value)}/>

            <h3>Image</h3>

            {image && (
                <img className={"block max-w-max rounded"} src={image} alt={"og:image"}/>
            )}

            <input type={"text"} value={image || ""} onChange={(e) => setImage(e.target.value)}/>

            <button onClick={send}>Submit</button>
        </DiaryLayout>
    )
}

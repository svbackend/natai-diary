export default function NarrowWrapper({children}: { children: any }) {
    return (
        <div className={"mx-auto flex flex-col max-w-lg rounded-md my-5"}>
            {children}
        </div>
    )
}
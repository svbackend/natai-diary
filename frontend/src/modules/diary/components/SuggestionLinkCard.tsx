import {SuggestionLinkDto} from "../../../api/apiSchemas";
import Link from "next/link";
import Image from "next/image";
import React from "react";
import * as ScrollArea from "@radix-ui/react-scroll-area";

export function SuggestionLinksCards(props: { links: SuggestionLinkDto[] }) {
    return (
        <ScrollArea.Root className="ScrollAreaRoot">
            <ScrollArea.Viewport className="ScrollAreaViewport">
                <div className="flex flex-nowrap mt-4 gap-4">
                    {props.links.map((link, i) =>
                        <SuggestionLinkCard key={i} link={link}/>
                    )}
                </div>
            </ScrollArea.Viewport>
            <ScrollArea.Scrollbar className="ScrollAreaScrollbar" orientation="horizontal">
                <ScrollArea.Thumb className="ScrollAreaThumb"/>
            </ScrollArea.Scrollbar>
            <ScrollArea.Corner className="ScrollAreaCorner"/>
        </ScrollArea.Root>
    )
}

export function SuggestionLinksCardsVisible(props: { links: SuggestionLinkDto[] }) {
    return (
        <div className="flex flex-nowrap mt-4 gap-4">
            {props.links.map((link, i) =>
                <SuggestionLinkCard key={i} link={link}/>
            )}
        </div>
    )
}

export function SuggestionLinkCard(props: { link: SuggestionLinkDto }) {
    return (
        <Link href={props.link.url} target="_blank" rel="noreferrer"
              className="flex flex-shrink-0 max-w-[60%] md:max-w-[70%] flex-col rounded-lg shadow-lg">
            {props.link.image && (
                <div className="flex-shrink-0">
                    <Image className="h-48 w-full object-cover rounded-lg" src={props.link.image} width={426}
                           height={240} alt=""/>
                </div>
            )}
            <div className="flex-1 bg-white dark:bg-dark dark:text-light p-6 flex flex-col justify-between">
                <div className="flex-1">
                    <p className="text-sm font-medium text-brand">
                        {props.link.title}
                    </p>
                    <p className="mt-3 text-base text-nav-item dark:text-nav-item-alt">
                        {props.link.description}
                    </p>
                </div>
            </div>
        </Link>
    )
}

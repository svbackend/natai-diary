import React, {useEffect, useState} from 'react';
import {CityDto} from "../../../api/apiSchemas";
import {fetchGetCities, fetchGetCitiesAutocomplete} from "../../../api/apiComponents";
import {cn} from "../../../utils/cn";

interface AutocompleteProps {
    suggestions: CityDto[];
    onCitySelected: (city: CityDto) => void;
    currentCity: CityDto | null;
}

const Autocomplete = ({suggestions, onCitySelected, currentCity}: AutocompleteProps) => (
    <ul className="absolute z-10 mt-2 w-full bg-white border border-gray-300 rounded shadow dark:bg-gray-800">
        {suggestions.map((city) => (
            <li
                key={city.id}
                onClick={() => onCitySelected(city)}
                className={cn("p-2 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer", city.id === currentCity?.id && "bg-gray-200 dark:bg-gray-600 font-bold")}>
                <span className="dark:text-gray-300">{city.name}</span>
            </li>
        ))}
    </ul>
);

interface CitySearchProps {
    currentCity: CityDto | null;
    onCitySelected: (city: CityDto) => void;
}

const CitySearch = (props: CitySearchProps) => {
    const [cities, setCities] = useState<CityDto[]>([]);
    const [searchTerm, setSearchTerm] = useState(props.currentCity?.name);
    const [autocompleteSuggestions, setAutocompleteSuggestions] = useState<CityDto[]>([]);

    useEffect(() => {
        fetchGetCities({})
            .then((res) => setCities(res.cities.slice(0, 6)))
            .catch(() => alert('Error fetching cities. Please try again later.'));
    }, []);

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const term = e.target.value;
        setSearchTerm(term);

        if (term.length >= 3) {
            fetchGetCitiesAutocomplete({queryParams: {q: term}})
                .then((res) => setAutocompleteSuggestions(res.cities))
                .catch(() => setAutocompleteSuggestions([]));
        } else {
            setAutocompleteSuggestions([]);
        }
    };

    const onSelect = (city: CityDto) => {
        setSearchTerm(city.name);
        setAutocompleteSuggestions([]);
        props.onCitySelected(city);
    };

    return (
        <div className="relative mb-5">
            <label htmlFor="name" className="text-sm font-medium text-nav-item dark:text-nav-item-alt">Location</label>
            <input
                type="text"
                className="w-full mt-2 p-2 border border-gray-300 rounded dark:bg-gray-800 dark:border-gray-600"
                placeholder="Search for a city..."
                value={searchTerm}
                onChange={handleSearchChange}
            />
            <p className="mt-1 text-sm text-gray-500">
                We use your location only to attach weather information to your notes.
            </p>
            {autocompleteSuggestions.length > 0 && <Autocomplete
                onCitySelected={onSelect}
                suggestions={autocompleteSuggestions}
                currentCity={props.currentCity}
            />}
        </div>
    );
};

export default CitySearch;
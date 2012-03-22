Ptm::Application.routes.draw do
  devise_for :users

  resources :albums do
    get 'my', :on => :collection

    resources :photos do
      resources :notes
    end
  end

  resources :friendlists

  root :to => 'home#index'
  match 'users', :to => 'home#users'
  match 'myfriends', :to => 'home#myfriends'
end

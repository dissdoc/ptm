Ptm::Application.routes.draw do
  devise_for :users

  resources :albums do
    get 'my', :on => :collection

    resources :photos do
      resources :notes
    end
  end

  resources :friendlists
  match '/friendlists/add_friend', :to => 'friendlists#add_friend', :via => :post
  match '/friendlists/remove_friend', :to => 'friendlists#remove_friend', :via => :post

  root :to => 'home#index'
  match 'users', :to => 'home#users'
  match 'myfriends', :to => 'home#myfriends'
end

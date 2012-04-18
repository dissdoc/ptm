class Album < ActiveRecord::Base
  belongs_to :user

  has_many :photos, :dependent => :destroy

  validates :name, :user_id, :presence => true
end
